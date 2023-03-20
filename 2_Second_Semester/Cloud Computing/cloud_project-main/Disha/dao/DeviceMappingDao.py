from datetime import datetime
from db.utils import DatabaseHelper


class DeviceMappingDao:
    @classmethod
    def get_device_mapping_by_sender_id(cls, sender_id):
        query_template = """
            SELECT * FROM device_mapping WHERE sender_id = {};
        """
        query = query_template.format(sender_id)
        with DatabaseHelper.get_connection() as conn:
            conn.row_factory = lambda c, r: dict(zip([c[0] for c in c.description], r))
            cur = conn.cursor()
            cur.execute(query)
            return cur.fetchone()

    @classmethod
    def update_device_mapping(cls, device_mapping):
        query_template = """
            UPDATE device_mapping
            SET receiver_ids_list='{receiver_ids_list}', updated_at='{updated_at}'
            WHERE sender_id={sender_id}
        """
        query = query_template.format(
            receiver_ids_list=str(device_mapping.get('receiver_ids_list')),
            sender_id=device_mapping.get('sender_id'),
            updated_at=str(datetime.now()),
        )
        with DatabaseHelper.get_connection() as conn:
            conn.execute(query)
            conn.commit()
            return cls.get_device_mapping_by_sender_id(device_mapping.get("sender_id"))

    @classmethod
    def create_device_mapping(cls, device):
        query_template = """
            INSERT INTO device_mapping (sender_id, receiver_ids_list, created_at, updated_at)
            VALUES ('{sender_id}', '{receiver_ids_list}', '{created_at}', '{updated_at}');
        """
        query = query_template.format(
            sender_id=device.get('sender_id'),
            receiver_ids_list=str(device.get('receiver_ids_list')),
            updated_at=str(datetime.now()),
            created_at=str(datetime.now())
        )
        print(query)
        with DatabaseHelper.get_connection() as conn:
            conn.execute(query)
            cur = conn.cursor()
            cur.execute("SELECT last_insert_rowid();")
            device_sender_id = cur.fetchone()
            conn.commit()
            return cls.get_device_mapping_by_sender_id(device_sender_id[0])
