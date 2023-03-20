DROP TABLE IF EXISTS device;
CREATE TABLE device (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    name TEXT NOT NULL,
    ip TEXT,
    port TEXT
);

DROP TABLE IF EXISTS device_mapping;
CREATE TABLE device_mapping (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_id INTEGER NOT NULL,
    receiver_ids_list TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);