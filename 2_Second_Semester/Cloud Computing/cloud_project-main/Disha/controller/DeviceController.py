from flask_restful import Resource, request
from constants.ApplicationError import ApplicationError
from dao.DeviceDao import DeviceDao


class DeviceController(Resource):
    #Get a Device
    def get(self, device_id):
        device = DeviceDao.get_device_by_id(device_id)
        return ApplicationError.INVALID_DEVICE_ID.build() if device is None else device

    #Create a Device
    def post(self):
        device = request.get_json()
        device_id = device.get("id")
        device_old = DeviceDao.get_device_by_id(device_id) if device_id else None
        if device_old is None:
            return DeviceDao.create_device(device)
        else:
            return DeviceDao.update_device(device)