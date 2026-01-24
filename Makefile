.PHONY: help test package docker-core ci node-install

help:
	@echo "Targets:"
	@echo "  test        - Run Java tests"
	@echo "  package     - Build jar (skip tests)"
	@echo "  docker-core - Build Docker image using Dockerfile.core"
	@echo "  node-install- Install Node dependencies"
	@echo "  ci          - Run a local CI-like sequence"

test:
	./mvnw -B test

package:
	./mvnw -B -DskipTests package

docker-core:
	docker build -f Dockerfile.core -t nexia-core:local .

node-install:
	cd nexia-bff && npm ci

ci: test package docker-core node-install
