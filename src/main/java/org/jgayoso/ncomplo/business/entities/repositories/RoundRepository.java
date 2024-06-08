package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.List;

import org.jgayoso.ncomplo.business.entities.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoundRepository 
        extends JpaRepository<Round,Integer> {
    
    public List<Round> findByCompetitionId(final Integer competitionId);

    public Round findByCompetitionIdAndName(final Integer competitionId, String name);
    
}
    