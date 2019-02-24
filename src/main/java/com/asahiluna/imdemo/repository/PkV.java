package com.asahiluna.imdemo.repository;

import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;

/**
 * tablestore 需要的PK和Value的builder
 */
public class PkV {
    public String pk;
    public PrimaryKeyValue primaryKeyValue;

    private PkV(Builder builder) {
        this.pk = builder.pk;
        this.primaryKeyValue = builder.primaryKeyValue;
    }

    public static Builder newPkV() {
        return new Builder();
    }

    public static final class Builder {
        private String pk;
        private PrimaryKeyValue primaryKeyValue;

        private Builder() {
        }

        public PkV build() {
            return new PkV(this);
        }

        public Builder pk(String pk) {
            this.pk = pk;
            return this;
        }

        public Builder primaryKeyValue(PrimaryKeyValue primaryKeyValue) {
            this.primaryKeyValue = primaryKeyValue;
            return this;
        }
    }
}
