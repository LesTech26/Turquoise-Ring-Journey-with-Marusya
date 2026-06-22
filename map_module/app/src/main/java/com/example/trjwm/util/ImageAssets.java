package com.example.trjwm.util;

import android.content.Context;

public final class ImageAssets {
    private ImageAssets() {
    }

    public static int drawableId(Context context, String resourceName) {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            return 0;
        }
        return context.getResources().getIdentifier(
                resourceName,
                "drawable",
                context.getPackageName()
        );
    }

    public static String mapImageName() {
        return "new_map";
    }

    public static String coatImageNameForDistrict(String districtId) {
        switch (districtId) {
            case "bolkhov":
                return "img_coa_bolkhov";
            case "verkhov":
                return "img_coa_verkhov";
            case "glazunov":
                return "img_coa_glazunov";
            case "dmitrov":
                return "img_coa_dmitrovsk";
            case "dolzhansk":
                return "img_coa_dolzhansk";
            case "zalego":
                return "img_coa_zalego";
            case "znamensky":
                return "img_coa_znamensky";
            case "kolpny":
                return "img_coa_kolpny";
            case "korsakov":
                return "img_coa_korsakov";
            case "krasnoz":
                return "img_coa_krasnoz";
            case "kromskoy":
                return "img_coa_kromskoy";
            case "livensky":
                return "img_coa_livensky";
            case "maloarh":
                return "img_coa_maloarkhangelsk";
            case "mcensk":
                return "img_coa_mcensk";
            case "novoderev":
                return "img_coa_novoderev";
            case "novosil":
                return "img_coa_novosil";
            case "orlovsky":
                return "img_coa_orlovsky";
            case "pokrov":
                return "img_coa_pokrov";
            case "sverdlov":
                return "img_coa_sverdlov";
            case "soskov":
                return "img_coa_soskov";
            case "trosna":
                return "img_coa_trosna";
            case "urick":
                return "img_gerb_naryshkino";
            case "hotynets":
                return "img_coa_hotynets";
            case "shablykino":
                return "img_coa_shablykino";
            default:
                return null;
        }
    }
}
