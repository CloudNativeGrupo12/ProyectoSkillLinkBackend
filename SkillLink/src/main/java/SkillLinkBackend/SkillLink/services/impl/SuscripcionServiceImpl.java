package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.models.dto.SuscripcionInteresDto;
import SkillLinkBackend.SkillLink.models.entities.SuscripcionInteres;
import SkillLinkBackend.SkillLink.models.requests.SuscribirRequest;
import SkillLinkBackend.SkillLink.repositories.SuscripcionInteresRepository;
import SkillLinkBackend.SkillLink.services.SuscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuscripcionServiceImpl implements SuscripcionService {

    private final SuscripcionInteresRepository repository;

    @Override
    @Transactional
    public SuscripcionInteresDto suscribir(SuscribirRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        SuscripcionInteres suscripcion = repository.findByEmailIgnoreCase(email).orElseGet(() -> {
            SuscripcionInteres nueva = new SuscripcionInteres();
            nueva.setEmail(email);
            return repository.save(nueva);
        });
        return aDto(suscripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionInteresDto> listar() {
        return repository.findAll().stream().map(this::aDto).toList();
    }

    private SuscripcionInteresDto aDto(SuscripcionInteres s) {
        return new SuscripcionInteresDto(s.getId(), s.getEmail(), s.getTipo(), s.getCreadoEn());
    }
}
