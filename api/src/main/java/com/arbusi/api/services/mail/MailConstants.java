package com.arbusi.api.services.mail;

import org.springframework.beans.factory.annotation.Value;

public class MailConstants {
    public static final String PASSWORD_RESET_ENDPOINT = "/password-reset/";
    public static final String PASSWORD_RESET_SUBJECT = "Slaptažodžio atstatymas";
    public static final String PASSWORD_RESET_TEXT =
            """ 
            Sveiki,
            Gavome prašymą atstatyti Jūsų slaptažodį. Norėdami sukurti naują slaptažodį, spauskite šią nuorodą:\n\n 
            %s\n\n 
            Jei Jūs neprašėte atstatyti slaptažodžio, tiesiog ignoruokite šį laišką.\n\n 
            Gražios dienos!
            """;
}
