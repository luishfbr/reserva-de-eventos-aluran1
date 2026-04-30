package com.example.reservadeeventosaluran1.util;

import java.text.Normalizer;


public class SlugUtil {

    public static String gerarSlug(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome de sala inválido.");
        }

        return Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-{2,}", "-");
    }

}
