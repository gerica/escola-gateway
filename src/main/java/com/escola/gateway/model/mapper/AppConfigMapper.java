package com.escola.gateway.model.mapper;


import com.escola.gateway.model.response.AppConfigResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.boot.info.BuildProperties;

@Mapper(componentModel = "spring")
public interface AppConfigMapper {

    // Exemplo de como você poderia mapear, adaptando-o ao seu método toOutput
//    @Mapping(source = "version", target = "version")
//    @Mapping(source = "time", target = "time")
    // Se `appDescription` for uma propriedade em BuildProperties ou for passada separadamente
    @Mapping(source = "appDescription", target = "description")
    AppConfigResponse toOutput(BuildProperties buildProperties, String appDescription);
}
