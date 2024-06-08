package org.jgayoso.ncomplo.business.entities.repositories;

import org.jgayoso.ncomplo.business.entities.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Integer> {

    public ForgotPasswordToken findByToken(final String token);

    public ForgotPasswordToken findByEmail(final String email);

    public ForgotPasswordToken findByLogin(final String login);

}
