package com.github.zhuyizhuo.ldap.repository;

import com.github.zhuyizhuo.ldap.entity.Person;
import org.springframework.data.repository.CrudRepository;

import javax.naming.Name;

public interface PersonRepository extends CrudRepository<Person, Name> {
}
