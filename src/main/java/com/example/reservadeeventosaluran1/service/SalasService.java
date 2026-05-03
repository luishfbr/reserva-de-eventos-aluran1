package com.example.reservadeeventosaluran1.service;

import com.example.reservadeeventosaluran1.database.model.SalasEntity;
import com.example.reservadeeventosaluran1.database.repository.ISalasRepository;
import com.example.reservadeeventosaluran1.dto.SalaDto;
import com.example.reservadeeventosaluran1.exception.BadRequestException;
import com.example.reservadeeventosaluran1.exception.NotFoundException;
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

    public SalasEntity findBySlug(String slug) {
        return salasRepository.findBySlug(slug);
    }

    public void insert(SalaDto salaDto) throws BadRequestException {
        String slug = SlugUtil.gerarSlug(salaDto.getNome());

        SalasEntity sala = salasRepository.findBySlug(slug);

        if (sala != null) {
            throw new BadRequestException("Sala já cadastrada.");
        }


        salasRepository.save(
                SalasEntity.builder()
                        .nome(salaDto.getNome())
                        .slug(slug)
                        .capacidade(salaDto.getCapacidade())
                        .ativo(salaDto.getAtivo())
                        .build()
        );
    }

    public void update(SalaDto salaDto, UUID id) throws NotFoundException {
        SalasEntity sala = salasRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sala não encontrada."));

        String slug = SlugUtil.gerarSlug(sala.getNome());

        sala.setNome(salaDto.getNome());
        sala.setSlug(slug);
        sala.setCapacidade(salaDto.getCapacidade());
        sala.setAtivo(salaDto.getAtivo());

        salasRepository.save(sala);
    }

    public void delete(UUID id) throws NotFoundException {
        SalasEntity sala = salasRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sala não encontrada."));

        salasRepository.deleteById(id);
    }

}
