from flask_restful import Resource
from constants.ApplicationError import ApplicationError
from dao.DeviceMappingDao import DeviceMappingDao
from dao.DeviceDao import DeviceDao
import json


class DeviceNetworkConfigController(Resource):
    # Return recipient device data after join
    def get(self, sender_id):
        device_mapping = DeviceMappingDao.get_device_mapping_by_sender_id(sender_id)
        if device_mapping is None or device_mapping.get('receiver_ids_list') is None:
            return ApplicationError.INVALID_DEVICE_ID.build()
        receiver_ids = json.loads(device_mapping.get('receiver_ids_list'))
        receiver_devices = DeviceDao.get_devices_by_ids(receiver_ids)
        return [] if receiver_devices is None else receiver_devices
