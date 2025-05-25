package be.ucll.unit.repository;

import be.ucll.model.Membership;
import be.ucll.repository.MembershipRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class MembershipRepositoryStub implements MembershipRepository {


    private List<Membership> memberships = new ArrayList<>();




    @Override
    public <S extends Membership> S save(S entity) {
         memberships.add(entity);
         return entity;
    }

    @Override
    public List<Membership> findAll() {
        return memberships;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Membership> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Membership> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Membership> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Membership getOne(Long aLong) {
        return null;
    }

    @Override
    public Membership getById(Long aLong) {
        return null;
    }

    @Override
    public Membership getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Membership> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Membership> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Membership> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Membership> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Membership> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Membership> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Membership, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }


    @Override
    public <S extends Membership> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Membership> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }



    @Override
    public List<Membership> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Membership entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Membership> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Membership> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Membership> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Membership findByUserEmailAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String email, LocalDate start, LocalDate end) {
        return memberships.stream()
                .filter(m -> m.getUser().getEmail().equals(email))
                .filter(m -> !m.getStartDate().isAfter(start)) // startDate <= start
                .filter(m -> {
                    LocalDate membershipEnd = m.getEndDate();
                    // If endDate is null, treat as ongoing (always valid)
                    return membershipEnd == null || !membershipEnd.isBefore(end); // endDate >= end OR endDate is null
                })
                .findFirst()
                .orElse(null);
    }

}
