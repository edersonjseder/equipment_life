package com.life.equipmentlife.model.networking.api;

import com.life.equipmentlife.model.pojo.AddressData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static com.life.equipmentlife.common.constants.Constants.JSON;
import static com.life.equipmentlife.common.constants.Constants.SLASH;


/**
 * Interface with the end point methods to be used to retrieve data from API
 */
public interface CepApi {

    /**
     * Loads the address data according to the CEP inserted
     *
     * @param cep the video ID of the movie
     * @return the address data
     */
    @GET("{cep}" + SLASH + JSON)
    Call<AddressData> loadAddressData(@Path("cep") String cep);

}
