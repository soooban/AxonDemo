package com.craftsman.eventsourcing.helper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Service
@AllArgsConstructor
@Slf4j
public class WorkerIdService {

    private final WorkerIdRepository workerIdRepository;


    Long getWorkerId() {

        String serviceKey = getServiceKey();

        WorkerId workerId = workerIdRepository.findByServiceKey(serviceKey);

        if (workerId != null) {
            return workerId.getId() % (SnowFlake.MAX_MACHINE_NUM + 1);
        }

        workerId = new WorkerId();
        workerId.setServiceKey(serviceKey);
        workerIdRepository.save(workerId);
        return workerId.getId() % (SnowFlake.MAX_MACHINE_NUM + 1);
    }

    /**
     * 由于 Spring Cloud Discovery 的 ServiceInstance 接口没有一个获取 instance id 的方法，所以只能想办法自己标记
     *
     * @return ip:mac_address 形式的字符串
     */
    public String getServiceKey() {
        byte[] mac = null;
        String hostAddress = null;
        try {

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && (networkInterface.getDisplayName().equals("en0") || networkInterface.getDisplayName().equals("eth0"))) {
                        hostAddress = addr.getHostAddress();
                        mac = networkInterface.getHardwareAddress();
                        break;
                    }
                }
                if (mac != null && StringUtils.isNotBlank(hostAddress)) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (mac == null || StringUtils.isBlank(hostAddress)) {
            return null;
        }
        // mac地址拼装成String
        StringBuilder macAddress = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                macAddress.append("-");
            }
            //mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            macAddress.append(s.length() == 1 ? 0 + s : s);
        }

        // 把字符串所有小写字母改为大写成为正规的mac地址并返回
        return hostAddress + ":" + macAddress.toString().toUpperCase();
    }
}


