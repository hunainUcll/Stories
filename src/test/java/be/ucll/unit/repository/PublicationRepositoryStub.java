package be.ucll.unit.repository;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PublicationRepositoryStub implements PublicationRepository {

    private List<Book> books;
    private List<Magazine> magazines;

    public PublicationRepositoryStub() {
        books = new ArrayList<>();
        magazines = new ArrayList<>();
    }

    public List<Publication> getAllPublications() {
        List<Publication> allPublications = new ArrayList<>();
        allPublications.addAll(books);
        allPublications.addAll(magazines);
        return allPublications;
    }

    @Override
    public <S extends Publication> S save(S entity) {
        if (entity instanceof Book) {
            books.add((Book) entity);
        } else if (entity instanceof Magazine) {
            magazines.add((Magazine) entity);
        }
        return entity;
    }

    @Override
    public List<Publication> findPublicationsByTitleContainingIgnoreCaseAndType(String title, String type) {
        return  getAllPublications().stream()
                .filter(pub -> (title == null || pub.getTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(pub -> (type == null || pub.getClass().getSimpleName().equalsIgnoreCase(type)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Publication> findPublicationsByTitleContainingIgnoreCase(String title) {
        return  getAllPublications().stream()
                .filter(pub -> (title == null || pub.getTitle().toLowerCase().contains(title.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Publication> findPublicationsByAvailableCopiesAfter(int availableCopies) {
        return   getAllPublications().stream()
                .filter(pub ->(pub.getAvailableCopies() >= availableCopies))
                .collect(Collectors.toList());
    }

    @Override
    public List<Publication> findAll() {
        List<Publication> allPublications = new ArrayList<>();
        allPublications.addAll(books);
        allPublications.addAll(magazines);
        return allPublications;
    }


    @Override
    public Optional<Publication> findById(Long id) {
        return getAllPublications().stream()
                .filter(pub -> pub.getId() != null && pub.getId().equals(id))
                .findFirst();
    }



    @Override
    public void flush() {

    }

    @Override
    public <S extends Publication> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Publication> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Publication> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Publication getOne(Long aLong) {
        return null;
    }

    @Override
    public Publication getById(Long aLong) {
        return null;
    }

    @Override
    public Publication getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Publication> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Publication> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Publication> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Publication> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Publication> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Publication> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Publication, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }



    @Override
    public <S extends Publication> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }


    @Override
    public boolean existsById(Long aLong) {
        return false;
    }



    @Override
    public List<Publication> findAllById(Iterable<Long> longs) {
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
    public void delete(Publication entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Publication> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Publication> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Publication> findAll(Pageable pageable) {
        return null;
    }


}

