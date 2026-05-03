package com.example.reservadeeventosaluran1.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SlugUtilTest {

    // ─── H: happy paths ─────────────────────────────────────────────────────

    @Test
    void gerarSlug_deveRetornarSlugSimples_quandoNomeSemCaracteresEspeciais() {
        assertThat(SlugUtil.gerarSlug("Sala de Reuniao")).isEqualTo("sala-de-reuniao");
    }

    @Test
    void gerarSlug_deveRemoverAcentos_quandoNomePossuiDiacriticos() {
        assertThat(SlugUtil.gerarSlug("Auditório Público")).isEqualTo("auditorio-publico");
    }

    @Test
    void gerarSlug_deveRemoverCaracteresEspeciais_quandoNomePossuiSimbologiaExtra() {
        // '&' não é [a-z0-9-] → removido; espaços → dashes; resultado colapsa dashes duplos
        assertThat(SlugUtil.gerarSlug("Ação & Reação")).isEqualTo("acao-reacao");
    }

    @Test
    void gerarSlug_deveColapsarMultiplosDashes_quandoHaEspacosOuCaracteresRemovidos() {
        assertThat(SlugUtil.gerarSlug("Sala  --  Especial")).isEqualTo("sala-especial");
    }

    @Test
    void gerarSlug_deveFazerTrim_quandoHaEspacosNasExtremidades() {
        assertThat(SlugUtil.gerarSlug("  sala de eventos  ")).isEqualTo("sala-de-eventos");
    }

    @Test
    void gerarSlug_deveConverterParaMinusculas_quandoNomeEmMaiusculas() {
        assertThat(SlugUtil.gerarSlug("SALA DE CONFERENCIA")).isEqualTo("sala-de-conferencia");
    }

    @Test
    void gerarSlug_deveManterNumeros_quandoNomePossuiDigitos() {
        assertThat(SlugUtil.gerarSlug("Sala 101")).isEqualTo("sala-101");
    }

    @Test
    void gerarSlug_deveSerIdempotente_quandoNomeJaESlug() {
        assertThat(SlugUtil.gerarSlug("sala-de-reuniao")).isEqualTo("sala-de-reuniao");
    }

    @Test
    void gerarSlug_deveRemoverAcentosPortuguesVariados() {
        assertThat(SlugUtil.gerarSlug("Área de Convivência")).isEqualTo("area-de-convivencia");
    }

    // ─── I: erros e edge cases ───────────────────────────────────────────────

    @Test
    void gerarSlug_deveLancarIllegalArgumentException_quandoNomeNulo() {
        assertThatThrownBy(() -> SlugUtil.gerarSlug(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome de sala inválido.");
    }

    @Test
    void gerarSlug_deveLancarIllegalArgumentException_quandoNomeVazio() {
        assertThatThrownBy(() -> SlugUtil.gerarSlug(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome de sala inválido.");
    }

    @Test
    void gerarSlug_deveLancarIllegalArgumentException_quandoNomeApenasBrancos() {
        // isBlank() cobre strings com apenas espaços/tabs, não só isEmpty()
        assertThatThrownBy(() -> SlugUtil.gerarSlug("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome de sala inválido.");
    }

    @Test
    void gerarSlug_deveRetornarStringVazia_quandoNomeSoTemCaracteresEspeciais() {
        // '!!!' não contém [a-z0-9-] → resultado vazio. Sem exceção — validar na camada de serviço.
        assertThat(SlugUtil.gerarSlug("!!!")).isEmpty();
    }
}
