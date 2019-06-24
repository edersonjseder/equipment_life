package com.life.equipmentlife.common.constants;

public interface Constants {

    String URL_BASE = "https://equipment-life.firebaseio.com/";

    String URL_CEP = "https://viacep.com.br/ws/";

    String URL_ESTADO = "https://servicodados.ibge.gov.br/api/v1/";

    String DATABASE_NAME = "equipmentsdb";
    String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";

    String JSON = "json";
    String SLASH = "/";

    String LOCALIDADES = "localidades";
    String ESTADOS = "estados";

    String UID = "UID";
    String PROFILE_ID = "id";
    String USER_NAME = "name";
    String PROFILE_CURRENT_OWNER_ID = "profileCurrentOwnerId";
    String PROFILE_PREVIOUS_OWNER_ID = "idPreviousOwner";
    String USER_EMAIL = "email";
    String USER_PICTURE = "picture";
    String DATA = "data";
    String URL = "url";
    String SERIAL = "serialNumber";
    String STATUS = "status";
    String STATUS_CURRENT_OWNER = "statusCurrentOwner";
    String STATUS_PREVIOUS_OWNER = "statusPreviousOwner";
    String BRAND = "brand";
    String CPF = "cpf";

    String CPF_PATTERN = "^((\\d{3}).(\\d{3}).(\\d{3})-(\\d{2}))*$";

    String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    String CPF_PATTERN_NUMBERS = "(?=^((?!((([0]{11})|([1]{11})|([2]{11})|([3]{11})|([4]{11})|([5]{11})|([6]{11})|([7]{11})|([8]{11})|([9]{11})))).)*$)([0-9]{11})";

    String EQUIPMENT_LIFE_CAM  = "equipmentlifecam";

    String EQUIPMENT = "equipment";
    String PROFILE = "profile";

    String EQUIPMENTS = "equipments";
    String PROFILES = "profiles";

    String AMOUNT_INSERTED_EQUIPMENT = "equipment_amount";

}
