package be.ucll.unit.repository;

import be.ucll.model.User;
import be.ucll.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class UserRepositoryStub implements UserRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public <S extends User> S save(S entity) {
        users.add(entity);
        return entity;
    }

    @Override
    public void delete(User entity) {
        users.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    // You can leave these unimplemented if unused
    @Override public Optional<User> findById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean existsById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override public long count() {
        throw new UnsupportedOperationException();
    }

    @Override public void deleteById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override public void deleteAll() {
        users.clear();
    }

    @Override public <S extends User> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(users::add);
        return (List<S>) users;
    }

    @Override public List<User> findAllById(Iterable<Long> longs) {
        throw new UnsupportedOperationException();
    }

    @Override public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(users::remove);
    }

    @Override
    public List<User> findUsersByAgeAfter(int i) {
        return List.of();
    }

    @Override
    public List<User> findUsersByAgeBetween(int ageAfter, int ageBefore) {
        return List.of();
    }

    @Override
    public List<User> findUserByName(String name) {
        return List.of();
    }

    @Override
    public List<User> findUsersByName(String name) {
        return List.of();
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return false;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

    @Override
    public void deleteUserByEmail(String email) {

    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public User getOne(Long aLong) {
        return null;
    }

    @Override
    public User getById(Long aLong) {
        return null;
    }

    @Override
    public User getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<User> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }
}
