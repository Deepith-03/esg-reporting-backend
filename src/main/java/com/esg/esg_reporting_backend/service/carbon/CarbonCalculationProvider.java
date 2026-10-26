package com.esg.esg_reporting_backend.service.carbon;

import java.util.List;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRequest;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationResponse;
import com.esg.esg_reporting_backend.dto.carbon.EmissionFactorDto;

public interface CarbonCalculationProvider {

    CarbonCalculationResponse calculate(CarbonCalculationRequest request);

    List<EmissionFactorDto> getAvailableFactors();

    String getProviderIdentifier();

    String getProviderDisplayName();

    boolean isOfficial();
}
