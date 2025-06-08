package com.zjfgh.bluedhook.simple;

import java.util.List;

public class JinShanDocApiResponse {
    private Data data;
    private String error;
    private String status;

    // Getters and Setters
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Data {
        private List<Log> logs;
        private String result;

        // Getters and Setters
        public List<Log> getLogs() {
            return logs;
        }

        public void setLogs(List<Log> logs) {
            this.logs = logs;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    public static class Log {
        private String filename;
        private String timestamp;
        private long unix_time;
        private String level;
        private List<String> args;

        // Getters and Setters
        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public long getUnix_time() {
            return unix_time;
        }

        public void setUnix_time(long unix_time) {
            this.unix_time = unix_time;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public List<String> getArgs() {
            return args;
        }

        public void setArgs(List<String> args) {
            this.args = args;
        }
    }
    public static class ResultData {
        private int code;
        private List<User> data;

        // Getters and Setters
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<User> getData() {
            return data;
        }

        public void setData(List<User> data) {
            this.data = data;
        }
    }
}
