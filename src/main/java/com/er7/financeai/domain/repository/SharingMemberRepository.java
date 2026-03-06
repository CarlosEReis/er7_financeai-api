package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.Group;
import com.er7.financeai.domain.model.GroupMember;
import com.er7.financeai.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
//
//public interface SharingMemberRepository extends JpaRepository<GroupMember, Long> {
//
//    /**
//     * 1. MÉTODO DE CRIAÇÃO/ATUALIZAÇÃO DE PERMISSÃO (Usado no SharingService)
//     * Busca um registro de permissão específico para evitar duplicidade.
//     * Esta é a chave para o método 'addOrUpdatePermission' no serviço.
//     * * @param group O contexto do grupo.
//     * @param targetUser O Dono dos Dados que concedeu a permissão.
//     * @param allowedUser O Usuário que está recebendo a permissão.
//     * @return O registro de permissão, se existir.
//     */
//    Optional<GroupMember> findByGroupAndTargetUserAndAllowedUser(
//            Group group,
//            User targetUser,
//            User allowedUser
//    );
//
//    /**
//     * 2. MÉTODO DE LEITURA DE PERMISSÕES (Usado na Query de Transações)
//     * Busca todas as permissões concedidas a um usuário específico.
//     * Isso será utilizado para construir a Query de Transações, pois
//     * precisamos saber todos os 'targetUsers' (donos dos dados) que o
//     * 'allowedUser' pode acessar.
//     * * @param allowedUser O usuário logado que está tentando ler os dados.
//     * @return Uma lista de objetos SharingMember que concedem acesso ao usuário logado.
//     */
//    List<GroupMember> findByAllowedUser(User allowedUser);
//}