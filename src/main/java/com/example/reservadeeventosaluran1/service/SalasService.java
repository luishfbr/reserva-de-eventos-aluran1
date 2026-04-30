package com.example.reservadeeventosaluran1.service;

import com.example.reservadeeventosaluran1.database.model.SalasEntity;
import com.example.reservadeeventosaluran1.database.repository.ISalasRepository;
import com.example.reservadeeventosaluran1.dto.SalaDto;
import com.example.reservadeeventosaluran1.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalasService {

    private final ISalasRepository salasRepository;

    public List<SalasEntity> findAll() {
        return salasRepository.findAll();
    }

    public List<SalasEntity> findAllBySlug(String slug) {
        return salasRepository.findAllBySlug(slug);
    }

    public void insert(SalaDto salaDto) {
        salasRepository.save(
                SalasEntity.builder()
                        .nome(salaDto.getNome())
                        .slug(SlugUtil.gerarSlug(salaDto.getNome()))
                        .capacidade(salaDto.getCapacidade())
                        .build()
        );
    }

    public void update(SalaDto salaDto, UUID id) {
        SalasEntity sala = salasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala não encontrada."));

        String slug = SlugUtil.gerarSlug(sala.getNome());

        sala.setNome(salaDto.getNome());
        sala.setSlug(slug);
        sala.setCapacidade(salaDto.getCapacidade());

        salasRepository.save(sala);
    }

    public void delete(UUID id) {
        salasRepository.deleteById(id);
    }

}
