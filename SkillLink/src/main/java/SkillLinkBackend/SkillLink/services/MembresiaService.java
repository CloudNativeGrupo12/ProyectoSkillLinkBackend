package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.MembresiaDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarMembresia;
import SkillLinkBackend.SkillLink.models.requests.AgregarMembresia;

import java.util.List;

public interface MembresiaService {

    List<MembresiaDto> listar();

    MembresiaDto obtenerPorId(Long id);

    MembresiaDto crear(AgregarMembresia request);

    MembresiaDto actualizar(Long id, ActualizarMembresia request);

    void eliminar(Long id);
}