class ErrorFormat:
    def __init__(self, msg):
        self.msg = msg

    def build(self):
        return {"error": self.msg}


class ApplicationError:
    INVALID_DEVICE_ID = ErrorFormat("Device ID missing or invalid")
