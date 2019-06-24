package com.life.equipmentlife.common.utils;

import java.util.regex.Pattern;

import static com.life.equipmentlife.common.constants.Constants.CPF_PATTERN;
import static com.life.equipmentlife.common.constants.Constants.CPF_PATTERN_NUMBERS;

public class CpfValidation {

    private static Pattern PATTERN_GENERIC = Pattern.compile(CPF_PATTERN);
    private static Pattern PATTERN_NUMBERS = Pattern.compile(CPF_PATTERN_NUMBERS);

    public static boolean isValid(String cpf) {

        if (cpf != null && PATTERN_GENERIC.matcher(cpf).matches()) {

            cpf = cpf.replaceAll("-|\\.", "");

            if (cpf != null && PATTERN_NUMBERS.matcher(cpf).matches()) {

                int[] numbers = new int[11];

                for (int i = 0; i < 11; i++) numbers[i] = Character.getNumericValue(cpf.charAt(i));

                int i;

                int sum = 0;

                int factor = 100;

                for (i = 0; i < 9; i++) {
                    sum += numbers[i] * factor;
                    factor -= 10;
                }

                int leftover = sum % 11;

                leftover = leftover == 10 ? 0 : leftover;

                if (leftover == numbers[9]) {

                    sum = 0;

                    factor = 110;

                    for (i = 0; i < 10; i++) {
                        sum += numbers[i] * factor;
                        factor -= 10;
                    }

                    leftover = sum % 11;

                    leftover = leftover == 10 ? 0 : leftover;

                    return leftover == numbers[10];

                }

            }

        }

        return false;

    }

}
