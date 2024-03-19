package pe.prima.com.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMailResponse implements Serializable {

    private static final long serialVersionUID = -1607695576074515548L;

    private WsResult wsResult;
    private WsError wsError;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static
    class WsResult implements Serializable {

        private static final long serialVersionUID = -1607695576074515548L;

        private Integer idResult;
        private String resultDescription;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static
    class WsError implements Serializable {

        private static final long serialVersionUID = 4238837776542557249L;

        private Integer idError;
        private String errorDescription;

    }

}