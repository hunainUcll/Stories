package be.ucll.unit.repository;

import be.ucll.model.Profile;
import be.ucll.repository.ProfileRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ProfileRepositoryStub implements ProfileRepository {


    private List<Profile> profiles = new ArrayList<>();

    @Override
    public <S extends Profile> S save(S entity) {
        profiles.add(entity);
        return entity;
    }


    @Override
    public void flush() {

    }

    @Override
    public <S extends Profile> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Profile> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Profile> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Profile getOne(Long aLong) {
        return null;
    }

    @Override
    public Profile getById(Long aLong) {
        return null;
    }

    @Override
    public Profile getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Profile> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Profile> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Profile> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Profile> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Profile> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Profile> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Profile, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }


    @Override
    public <S extends Profile> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Profile> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Profile> findAll() {
        return List.of();
    }

    @Override
    public List<Profile> findAllById(Iterable<Long> longs) {
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
    public void delete(Profile entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Profile> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Profile> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Profile> findAll(Pageable pageable) {
        return null;
    }
}
