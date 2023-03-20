from flask_restful import Resource, request
from constants.ApplicationError import ApplicationError
from dao.DeviceMappingDao import DeviceMappingDao


class DeviceMappingController(Resource):
    #Get Device ID's Mapped Array
    def get(self, sender_id):
        device_mapping = DeviceMappingDao.get_device_mapping_by_sender_id(sender_id)
        return ApplicationError.INVALID_DEVICE_ID.build() if device_mapping is None else device_mapping

    #Create Device Mapping
    def post(self):
        device_mapping = request.get_json()
        sender_id = device_mapping.get("sender_id")
        device_old = DeviceMappingDao.get_device_mapping_by_sender_id(sender_id) if sender_id else None
        print(device_old)
        if device_old is None:
            return DeviceMappingDao.create_device_mapping(device_mapping)
        else:
            return DeviceMappingDao.update_device_mapping(device_mapping)
