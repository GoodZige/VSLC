package com.vslc.model;

import com.vslc.enums.TransferMethodEnum;
import com.vslc.enums.TransferStatusEnum;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenlele
 * 2018/4/14 12:49
 */
public class FileTransferRecord {

    private Integer method;

    private String ip;

    private Integer port;

    private String ua;

    private String userAccount;

    private String filePath;

    private String length;

    private Integer status;

    private String startTime;

    private String endTime;

    public FileTransferRecord(HttpServletRequest request) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.ip = request.getRemoteAddr();
        this.port = request.getRemotePort();
        this.ua = request.getHeader("user-agent");
        this.startTime = timeFormat.format(new Date());
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        String statusValue = null;
        String methodValue = null;
        for (TransferMethodEnum method : TransferMethodEnum.values()) {
            if (this.method == method.getCode()) {
                methodValue = method.getMessage();
                break;
            }
        }
        for (TransferStatusEnum status : TransferStatusEnum.values()) {
            if (this.status == status.getCode()) {
                statusValue = status.getMessage();
                break;
            }
        }
        return "FileTransferRecord{" +
                "method='" + methodValue + '\'' +
                ",ip='" + ip + '\'' +
                ",port=" + port +
                ",ua='" + ua + '\'' +
                ",userAccount='" + userAccount + '\'' +
                ",filePath='" + filePath + '\'' +
                ",length=" + length +
                ",status=" + statusValue + '\'' +
                ",startTime=" + startTime +
                ",endTime=" + endTime +
                '}';
    }
}
