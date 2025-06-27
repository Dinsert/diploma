package ru.skypro.homework.util;

import ru.skypro.homework.dto.CreateOrUpdateAd;

public class AdFixture {

    public static final int adId = 1;
    public static final int incorrectAdId = 0;

    public static CreateOrUpdateAd getCreateAd() {
        CreateOrUpdateAd createAd = new CreateOrUpdateAd();
        createAd.setTitle("Sell phone");
        createAd.setDescription("Phone is good");
        createAd.setPrice(3000);
        return createAd;
    }

    public static CreateOrUpdateAd getUpdateAd() {
        CreateOrUpdateAd updateAd = new CreateOrUpdateAd();
        updateAd.setTitle("updTitle");
        updateAd.setDescription("updDescription");
        updateAd.setPrice(123);
        return updateAd;
    }
}
