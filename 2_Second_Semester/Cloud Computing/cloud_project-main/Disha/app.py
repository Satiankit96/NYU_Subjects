from flask import Flask
from flask_restful import Api

from controller.DeviceController import DeviceController
from controller.DeviceMappingController import DeviceMappingController
from controller.DeviceNetworkConfigController import DeviceNetworkConfigController

app = Flask(__name__)
api = Api(app)

api.add_resource(DeviceController, '/device/', '/device/<int:device_id>')
api.add_resource(DeviceMappingController, '/device-mapping/', '/device-mapping/<int:sender_id>')
api.add_resource(DeviceNetworkConfigController, '/device-network-mapping/<int:sender_id>')

if __name__ == '__main__':
    app.run()
