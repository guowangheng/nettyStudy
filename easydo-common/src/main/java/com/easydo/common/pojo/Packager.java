package com.easydo.common.pojo;

import java.io.Serializable;

public class Packager  implements Serializable {

    private static final long serialVersionUID = 7543514951950971498L;

    private int state;                  // 1代表接口调用,2代表心跳包

    public Packager() {
    }

    public Packager(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Packager{" +
                "state=" + state +
                '}';
    }
}
