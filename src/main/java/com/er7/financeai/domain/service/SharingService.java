package com.er7.financeai.domain.service;

import com.er7.financeai.domain.repository.GroupMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class SharingService {

    private final GroupMemberRepository groupMemberRepository;

    public SharingService(
            GroupMemberRepository groupMemberRepository
    ) {
        this.groupMemberRepository = groupMemberRepository;
    }

    /**
     * Busca o grupo pertencente ao usuário ou cria um novo se não existir.
     */
//    private Group findOrCreateGroup(User owner) {
//        return groupMemberRepository.findByOwner(owner)
//                .orElseGet(() -> {
//                    Group novo = new Group();
//                    novo.setOwner(owner);
//                    // Salva o novo grupo
//                    return null;//groupMemberRepository.save(novo);
//                });
//    }

}