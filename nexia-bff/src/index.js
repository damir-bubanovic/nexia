const express = require("express");
const axios = require("axios");
const morgan = require("morgan");

const app = express();
app.use(express.json());
app.use(morgan("combined"));

const PORT = process.env.PORT || 3001;

// Call Java core directly inside Docker network (avoid gateway->bff->gateway loops)
const CORE_URL = process.env.NEXIA_CORE_URL || "http://localhost:8081";

function sendAxiosError(res, e) {
  if (e.response) return res.status(e.response.status).json(e.response.data);
  return res.status(500).json({ message: "bff_error", detail: String(e.message || e) });
}

// Health
app.get("/bff/health", (req, res) => {
  res.json({ status: "ok", service: "nexia-bff" });
});

// Simple “BFF proxy” endpoints for frontend usage
app.post("/bff/auth/register", async (req, res) => {
  try {
    const r = await axios.post(`${CORE_URL}/api/auth/register`, req.body, {
      headers: { "Content-Type": "application/json" }
    });
    res.status(r.status).json(r.data);
  } catch (e) {
    sendAxiosError(res, e);
  }
});

app.post("/bff/auth/login", async (req, res) => {
  try {
    const r = await axios.post(`${CORE_URL}/api/auth/login`, req.body, {
      headers: { "Content-Type": "application/json" }
    });
    res.status(r.status).json(r.data);
  } catch (e) {
    sendAxiosError(res, e);
  }
});

// Aggregated endpoint: login + fetch current user ("me") via /api/v1/users/me
app.post("/bff/auth/login-and-me", async (req, res) => {
  try {
    // 1) login
    const loginResp = await axios.post(`${CORE_URL}/api/auth/login`, req.body, {
      headers: { "Content-Type": "application/json" }
    });

    const token = loginResp.data?.accessToken || loginResp.data?.token;
    if (!token) {
      return res.status(502).json({
        message: "bff_error",
        detail: "Core login response did not include accessToken",
        loginResponse: loginResp.data
      });
    }

    // 2) fetch current user (server derives email from token)
    const meResp = await axios.get(`${CORE_URL}/api/v1/users/me`, {
      headers: { Authorization: `Bearer ${token}` }
    });

    // 3) return combined result
    res.json({
      accessToken: token,
      tokenType: loginResp.data?.tokenType || "Bearer",
      expiresInSeconds: loginResp.data?.expiresInSeconds,
      me: meResp.data
    });
  } catch (e) {
    sendAxiosError(res, e);
  }
});

// Current user ("me") using Authorization header (no password/email in body)
app.get("/bff/users/me", async (req, res) => {
  try {
    const auth = req.headers.authorization || "";
    if (!auth.trim()) return res.status(401).json({ message: "missing_authorization" });

    const r = await axios.get(`${CORE_URL}/api/v1/users/me`, {
      headers: { Authorization: auth }
    });

    res.status(r.status).json(r.data);
  } catch (e) {
    sendAxiosError(res, e);
  }
});

app.listen(PORT, () => {
  console.log(`nexia-bff listening on ${PORT} (CORE: ${CORE_URL})`);
});
