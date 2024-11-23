package com.veviosys.vdigit.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NativeQueriesUtils {

    public static String validColumn(String name) {
        return name;
    //  boolean valid = Pattern.matches("^[a-zA-Z][a-zA-Z0-9_]*$", name);

        //if (!valid)
        //  throw new NonValidDataException("not_valid_data : " + name);

        //return name;

    }

    public static String wildCard(String val) {


        return "%" + val.trim().replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_")
                .replace("[", "![") + "%" ;

    }

    public static void setParamToPreparedStatement(PreparedStatement stm, int paramNumber, Object value) throws SQLException {

        
        if(Objects.isNull(value))
        {
            stm.setNull(paramNumber, 0);
        }
        else if (value instanceof String) {
            stm.setString(paramNumber, (String) value);
            log.debug("setting qeury info... param String  N " + paramNumber + ", value = " + value );
        } else if (value instanceof Integer) {
            log.debug("setting qeury info... param Integer N " + paramNumber + ", value = " + value );
            stm.setInt(paramNumber, (Integer) value);
        }
    
        
    }
    
    

}