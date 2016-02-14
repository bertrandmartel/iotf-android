# IoT Foundation for Android

[![Build Status](https://travis-ci.org/akinaru/iotf-android.svg?branch=master)](https://travis-ci.org/akinaru/iotf-android)
[![Download](https://api.bintray.com/packages/akinaru/maven/iotf-android/images/download.svg) ](https://bintray.com/akinaru/maven/iotf-android/_latestVersion)
[![License](http://img.shields.io/:license-mit-blue.svg)](LICENSE.md)

IBM IoT foundation Messaging library for Android

## Bluemix setup

To use this library, you should have a Bluemix account and setup the following : 
* `Internet of Things Foundation Starter` application
* `Internet of Things Foundation` service<br/>
The latter service should be bound to the application and your application should be running.

To find all your credentials and parameters necessary to build an Android IoT Foundation project go to dashboard in `Internet of Things Foundation` service

From there you can manage :

* creation of device that will give you : `DEVICE_ID`, `DEVICE_TYPE` and `AUTHENTICATION_TOKEN`
* creation of access token that will give you : `API_KEY`,`API_TOKEN`
* get organization id from the main page `IOT_ORG`
* In beta : creation of gateway that will give you : `GATEWAY_TYPE`, `GATEWAY_ID` and `AUTHENTICATION_TOKEN`

## Include into your project

```
compile 'akinaru:iotf-android:0.1'
```

## Usage

### Applications

* Application

```
mHandler = new AppHandler(context, IOT_ORG, "MyApplicationID", API_KEY, API_TOKEN);
```

* Scalable Application

```
mHandler = new AppScalableHandler(context, IOT_ORG, "MyApplicationID", API_KEY, API_TOKEN);
```

`ApplicationID` is a user defined unique string identifier for the client.<br/>
`ApplicationID` cant be empty and cannot contain following characters : 
* +
* /
* :
* #

### Devices

```
mHandler = new DeviceHandler(context, IOT_ORG, DEVICE_TYPE, DEVICE_ID, AUTHENTICATION_TOKEN);
```

### Gateway (still in Beta)

```
mHandler = new GatewayHandler(context, IOT_ORG, GATEWAY_TYPE, GATEWAY_ID, AUTHENTICATION_TOKEN);
```

### MQTT callback

With MQTT callback, you get all events about connections success/failure, messages delivered/received :

```
mIotCallback = new IMessageCallback() {

    @Override
    public void connectionLost(Throwable cause) {
        // connection is lost
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        // a message has arrived
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken messageToken) {
        // a message has been delivered to server
    }

    @Override
    public void onConnectionSuccess() {
        // connection success
    }

    @Override
    public void onConnectionFailure() {
    	// connection failure
    }

    @Override
    public void onDisconnectionSuccess() {
    	// disconnection successfull
    }

    @Override
    public void onDisconnectionFailure() {
    	// disconnection has failed
    }
};

mHandler.addIotCallback(mIotCallback);
```

### Connection / Disconnection

* Connect

```
mHandler.connect();
```

* Connect with SSL support

```
mHandler.setSSL(true);
mHandler.connect();
```

* Disconnect

```
mHandler.disconnect();
```

### Subscribe

All subscription API have two version : one with `boolean isRetained`, `QosPolicy qos`, `IMqttActionListener completionListener` and one version without these parameters :

* For Applications and Scalable Applications

|  API                                                                      |   description                              |
|-------------------------------------------------------------------------------------|--------------------------------------------|
| subscribeDeviceEvents(String deviceType, String deviceId, String eventId)           | subscribe to device events                 |
| subscribeDeviceCommands(String deviceType, String deviceId, String commandId)       | subscribe to device commands               |
| subscribeDeviceStatus(String deviceType, String deviceId)                           | subscribe to device status                 |
| subscribeApplicationStatus(String appId)                                            | subscribe to application status            |

* For Devices

|  API prototype                                                                      |   description                              |
|-------------------------------------------------------------------------------------|--------------------------------------------|
| subscribeCommand(String commandId) |  subscribe to command ID|

* For Gateways (Beta)

|  API                                                                      |   description                              |
|-------------------------------------------------------------------------------------|--------------------------------------------|
| subscribeCommands(String deviceType, String deviceId, String commandId) |  subscribe to device command ID |

wildcard "+" can be used to match all type of a specific parameter

```
mHandler.subscribeDeviceEvents("+", "+", "+");
```
will subscribe to all deviceType / device ID / eventId

<hr/>

### Unsubscribe

* For applications and Scalable Applications

|  API                                                                      |   description                              |
|-------------------------------------------------------------------------------------|--------------------------------------------|
| unsubscribeDeviceEvents(String deviceType, String deviceId, String eventId)     | unsubscribe device events                |
| unsubscribeDeviceCommands(String deviceType, String deviceId, String commandId) | unsubscribe device commands              |
| unsubscribeDeviceStatus(String deviceType, String deviceId) |  unsubscribe device status |
| unsubscribeApplicationStatus(String appId) | unsubscribe application status |

* For Devices

|  API                                                                      |   description                              |
|-------------------------------------------------------------------------------------|--------------------------------------------|
| void unsubscribeCommand(String commandId) |  unsubscribe command ID |

* For Gateways (Beta)

|  API prototype                                                                      |   description                              |
|-------------------------------------------------------------------------------------|--------------------------------------------|
| unsubscribeCommands(String deviceType, String deviceId, String commandId) |  subscribe to device command ID |

wildcard "+" can be used to match all type of a specific parameter

```
mHandler.unsubscribeDeviceEvents("+", "+", "+");
```
will unsubscribe all deviceType / device ID / eventId

<hr/>

### Publish

All subscription API have two version : one with `QosPolicy qos`, `IMqttActionListener completionListener` and one version without these parameters :

* For applications

|  API                                                                      |   description                              |
|-------------------------------------------------------------------------------------|--------------------------------------------|
| publishDeviceEvents(String deviceType, String deviceId, String eventId, String message) | publish a device event on behalf of the device |
| publishDeviceCommands(String deviceType, String deviceId, String commandId, String message) | publish device commands |

* For Devices

|  API                                                                      |   description                              |
|-------------------------------------------------------------------------------------|--------------------------------------------|
| publishEvent(String eventId, String message) |  publish an event |

* For Gateway (Beta)

|  API                                                                      |   description                              |
|-------------------------------------------------------------------------------------|--------------------------------------------|
| publishOwnEvents(String eventId, String message) | publish an event on behalf of the gateway itself |
| publishDeviceEvents(String deviceType, String deviceId, String eventId, String message) | publish an event on behalf of a device |

### Connection parameters

* set QOS (default QosPolicy.QOS_POLICY_AT_MOST_ONCE)

```
mHandler.setQos(QosPolicy.QOS_POLICY_AT_MOST_ONCE);
```

possible values :

|  enum value   |   qos value  |
|---------------|--------------|
| QOS_POLICY_AT_MOST_ONCE | 0  |
| QOS_POLICY_AT_LEAST_ONCE | 1 |
| QOS_POLICY_ONCE | 2 |

* set keep alive in seconds (default 60s)

```
mHandler.setKeepAlive(60);
```

* set connection timeout in seconds (default 30s)

```
mHandler.setConnectionTimeout(30);
```

* set clean session (default `false`)

```
mHandler.setCleanSession(false);
```

## Complete Application example 


This example include an automatic reconnection mechanism when connection is lost. <br/>This will also subscribe to all device events when connected to server : 

```
private AppHandler mHandler;

private boolean exit = false;

private IMessageCallback mIotCallback;

@Override
protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connect);

	mHandler = new AppHandler(this, BuildConfig.BLUEMIX_IOT_ORG, getPackageName(), BuildConfig.BLUEMIX_API_KEY, BuildConfig.BLUEMIX_API_TOKEN);

	mIotCallback = new IMessageCallback() {
	    @Override
	    public void connectionLost(Throwable cause) {
	        Log.i(TAG, "connection lost");
	        if (cause != null) {
	            Log.e(TAG, "connection lost : " + cause.getMessage());
	        }
	        if (!exit) {
	            Log.i(TAG, "trying to reconnect");
	            mHandler.connect();
	        } else {
	            Log.i(TAG, "not trying to reconnect");
	        }
	    }

	    @Override
	    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
	        Log.i(TAG, "messageArrived : " + topic + " : " + new String(mqttMessage.getPayload()));
	    }

	    @Override
	    public void deliveryComplete(IMqttDeliveryToken messageToken) {
	        try {
	            Log.i(TAG, "deliveryComplete : " + new String(messageToken.getMessage().getPayload()));
	        } catch (MqttException e) {
	            e.printStackTrace();
	        }
	    }

	    @Override
	    public void onConnectionSuccess() {
	        Log.i(TAG, "subscribe to device events ...");
	        mHandler.subscribeDeviceEvents("+", "+", "+");
	    }

	    @Override
	    public void onConnectionFailure() {
			// connection failure
	    }

	    @Override
	    public void onDisconnectionSuccess() {
	    	// disconnection successfull
	        if (exit) {
	            mHandler.removeCallback(mIotCallback);
	        }
	    }

	    @Override
	    public void onDisconnectionFailure() {
			// disconnection has failed
	    }
	};

	mHandler.addIotCallback(mIotCallback);

	mHandler.setSSL(true);
}

public void onResume() {
    super.onResume();
    Log.d(TAG, "connecting");
    mHandler.connect();
}

public void onPause() {
    exit = true;
    Log.d(TAG, "disconnecting");
    mHandler.disconnect();
    super.onPause();
}
```

## IBM Messaging Documentation 

* https://github.com/ibm-messaging/iotf-rtd/blob/master/docs/getting_started/concepts.rst
* https://github.com/ibm-messaging/iotf-rtd/blob/master/docs/applications/mqtt.rst
* https://github.com/ibm-messaging/iotf-rtd/blob/master/docs/devices/mqtt.rst
* https://github.com/ibm-messaging/iotf-rtd/blob/master/docs/gateways/mqtt.rst

## External Libraries

* <a href="http://www.eclipse.org/paho/">Eclipse Paho MQTT open source client</a>
* <a href="https://commons.apache.org/proper/commons-net/">Apache commons-net</a>

## License

The MIT License (MIT) Copyright (c) 2016 Bertrand Martel
