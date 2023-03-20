from datetime import datetime
from db.utils import DatabaseHelper


class DeviceDao:
    @classmethod
    def get_device_by_id(cls, id):
        query_template = """
            SELECT * FROM device WHERE id = {};
        """
        query = query_template.format(id)
        with DatabaseHelper.get_connection() as conn:
            conn.row_factory = lambda c, r: dict(zip([c[0] for c in c.description], r))
            cur = conn.cursor()
            cur.execute(query)
            rows = cur.fetchall()
            return rows[0] if len(rows) > 0 else None

    @classmethod
    def get_devices_by_ids(cls, ids):
        query_template = """
            SELECT * FROM device WHERE id IN ({});
        """
        query = query_template.format(", ".join(map(str, ids)))
        with DatabaseHelper.get_connection() as conn:
            conn.row_factory = lambda c, r: dict(zip([c[0] for c in c.description], r))
            cur = conn.cursor()
            cur.execute(query)
            return cur.fetchall()

    @classmethod
    def update_device(cls, device):
        query_template = """
            UPDATE device
            SET name='{name}', port='{port}', ip='{ip}', updated_at='{updated_at}'
            WHERE id = {id}
        """
        query = query_template.format(
            name=device.get('name'),
            ip=device.get('ip'),
            port=device.get('port'),
            updated_at=str(datetime.now()),
            id=device.get('id')
        )
        with DatabaseHelper.get_connection() as conn:
            conn.execute(query)
            conn.commit()
            return cls.get_device_by_id(device.get("id"))

    @classmethod
    def create_device(cls, device):
        query_template = """
            INSERT INTO device (name, ip, port, created_at, updated_at)
            VALUES ('{name}', '{ip}', '{port}', '{created_at}', '{updated_at}');
        """
        query = query_template.format(
            name=device.get('name'),
            ip=device.get('ip'),
            port=device.get('port'),
            updated_at=str(datetime.now()),
            created_at=str(datetime.now())
        )
        with DatabaseHelper.get_connection() as conn:
            conn.execute(query)
            cur = conn.cursor()
            cur.execute("SELECT last_insert_rowid();")
            device_id = cur.fetchone()
            conn.commit()
            return cls.get_device_by_id(device_id[0])
