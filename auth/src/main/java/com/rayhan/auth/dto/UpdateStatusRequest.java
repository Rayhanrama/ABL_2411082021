package com.rayhan.auth.dto;

/**
 * Permintaan admin untuk mengubah status aktif/nonaktif user.
 */
public class UpdateStatusRequest {

    private boolean aktif;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(boolean aktif) {
        this.aktif = aktif;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }
}
