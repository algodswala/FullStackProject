package com.mvc.mysql.serviceImplementation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.mvc.mysql.entity.DistributorEntity;
import com.mvc.mysql.entity.InventoryEntity;
import com.mvc.mysql.exception.BadRequestException;
import com.mvc.mysql.exception.InternalServerException;
import com.mvc.mysql.exception.ResourceNotFound;
import com.mvc.mysql.model.DistributorMV;
import com.mvc.mysql.model.DistributorVM;
import com.mvc.mysql.repo.DistributorRepository;
import com.mvc.mysql.service.DistributorService;

@Service("DistributorService")
public class DistributorServiceImplementation implements DistributorService {
	@Autowired
	DistributorRepository DistributorRepository;

	@Autowired
	ModelMapper modelMapper;

	private String password;

	final static Logger logger = LoggerFactory.getLogger(DistributorServiceImplementation.class);

	/**
	 * @description get all distributors
	 */
	@Override
	public List<DistributorMV> getAllDistributor()
			throws InternalServerException, ResourceNotFound, BadRequestException {
		try {
			logger.info("get all distributor...");

			List<DistributorEntity> distributors = new ArrayList<>();

			DistributorRepository.findAll().forEach(distributors::add);
			Type listType = new TypeToken<List<InventoryEntity>>() {
			}.getType();

			if (distributors.isEmpty()) {

				throw new ResourceNotFound("Distributor not found");
			}

			else {
				return modelMapper.map(distributors, listType);
			}
		}

		catch (Exception e) {

			throw new InternalServerException("Server Error");
		}
	}

	/**
	 * @description Create distributors
	 */
	@Override
	public ResponseEntity<DistributorMV> postDistributor(DistributorVM distributor)
			throws BadRequestException, InternalServerException {
		try {
			logger.info("create distributor...");
			if (distributor == null) {
				throw new BadRequestException("You can't send null in fields..");
			} else {

				DistributorEntity c = modelMapper.map(distributor, DistributorEntity.class);
				Long i = (long) 4;
				String encrypted = encrypt(distributor.getPassword());
				c.setPassword(encrypted);

				DistributorEntity distributorEntity = DistributorRepository.save(c);
				return new ResponseEntity<DistributorMV>(modelMapper.map(distributorEntity, DistributorMV.class),
						HttpStatus.OK);
				// return modelMapper.map(_customer, DistributorMV.class);

			}

		} catch (Exception e) {
			throw new InternalServerException("Internal Server Error");
		}

	}

	/**
	 * @description update distributors
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity<DistributorMV> updateDistributor(@PathVariable("id") long id,
			@RequestBody DistributorVM distributor) throws BadRequestException, InternalServerException {
		try {
			if (distributor == null) {
				throw new BadRequestException("You can't send null in fields..");
			}
			logger.info("Update Distributor...");

			Optional<DistributorEntity> distributorEntity = DistributorRepository.findById(id);

			if (distributorEntity.isPresent()) {
				DistributorEntity _distributor = distributorEntity.get();
				_distributor.setName(distributor.getName());

				return new ResponseEntity<DistributorMV>(
						(MultiValueMap<String, String>) DistributorRepository.save(_distributor), HttpStatus.OK);
			} else {
				throw new ResourceNotFound("Distributor not found");
			}
		} catch (Exception e) {
			throw new InternalServerException("Internal Server Error");
		}
	}

	/**
	 * @description delete distributor by id
	 */
	@Override
	public ResponseEntity<String> deleteDistributor(@PathVariable("id") long id) throws ResourceNotFound {
		logger.info("delete customer...");
		if (DistributorRepository.existsById(id)) {
			DistributorRepository.deleteById(id);
			return new ResponseEntity<>("DistributorData has been deleted!", HttpStatus.OK);

		} else {
			throw new ResourceNotFound("Distributor id not found..");

		}

	}

	/**
	 * @description login distributors
	 */
	@Override
	public ResponseEntity<String> loginDistributor(DistributorVM distributor)
			throws BadRequestException, InternalServerException {
		// TODO Auto-generated method stub
		try {
			if (distributor == null) {
				throw new BadRequestException("You can't send null in fields..");

			}

			logger.info("login customer...");
			List<DistributorEntity> distributorData = DistributorRepository.findByNameAndPassword(distributor.getName(),
					encrypt(distributor.getPassword()));
			if (distributorData.isEmpty()) {
				return new ResponseEntity<>("Login Unsuccessful!", HttpStatus.NOT_FOUND);
			} else {

				return new ResponseEntity<>("successfully loged in!", HttpStatus.OK);
			}
		} catch (Exception e) {
			throw new InternalServerException("Internal Server Error");

		}
	}

	public String encrypt(String password) {

		this.password = password;

		return "crd56" + this.password + "!@#awfs88";
	}

}
