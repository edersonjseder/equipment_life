package com.life.equipmentlife.model.networking.api;

import com.life.equipmentlife.model.pojo.AddressData;
import com.life.equipmentlife.model.pojo.Estado;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static com.life.equipmentlife.common.constants.Constants.ESTADOS;
import static com.life.equipmentlife.common.constants.Constants.JSON;
import static com.life.equipmentlife.common.constants.Constants.LOCALIDADES;
import static com.life.equipmentlife.common.constants.Constants.SLASH;


/**
 * Interface with the end point methods to be used to retrieve data from API
 */
public interface EstadoApi {

    /**
     * Loads the estados list
     *
     * @return the estados list
     */
    @GET(LOCALIDADES + SLASH + ESTADOS)
    Call<List<Estado>> loadEstadosList();

}
