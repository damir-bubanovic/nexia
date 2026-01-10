create table if not exists processed_messages (
      event_id uuid primary key,
      processed_at timestamptz not null default now()
);
