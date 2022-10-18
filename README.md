# TAPO smartplug prometheus monitoring
Tapo smart pulug advanced monitoring for prometheus &amp; grafana

### How to use it - source code?

This is a spring boot application that has a RESTful API and Prometheus metrics for better monitoring of smart sockets Smart Plug metrics for TP-Link Tapo P110.

To run from the source code, just run the 

```online.labmaster.taposmartplug.TapoSmartplugApplication``` 

class with the following virtual machine parameters

```-Dtapo.plug.username=username -Dtapo.plug.password=password -Dtapo.plug.IPs=plug ip1,plug ip2```

 - tapo.plug.username: **name for tplink account**
 - tapo.plug.password: **password to the tplink account**
 - tapo.plug.IPs: **ip addresses of sockets**

WARNING! the application must run on the same network as the sockets


### How to use it - docker?

Jednoduché spustenie pomocou príkazu

```docker run -p 8080:8080 -e JAVA_OPTS="-Dtapo.plug.username=username -Dtapo.plug.password=password -Dtapo.plug.IPs=plug ip1,plug ip2" mirorucka/tapo-smartplug:1.0.1```

Parametre sú popísané vyššie

### Prometheus metrics

metrics are available at host:8080/actuator/prometheus

all metrics begin with the prefix tapo_

current list

| name of metric                |
|-------------------------------|
| tapo_energyUsage_currentPower |
| tapo_energyUsage_todayEnergy  |
| tapo_energyUsage_monthEnergy  |
| tapo_energyUsage_todayRuntime |
| tapo_energyUsage_monthRuntime |
| tapo_deviceInfo_on_time       |
| tapo_deviceInfo_rssi          |
| tapo_deviceInfo_device_on     |
