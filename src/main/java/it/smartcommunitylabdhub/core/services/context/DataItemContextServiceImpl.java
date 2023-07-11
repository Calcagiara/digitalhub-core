package it.smartcommunitylabdhub.core.services.context;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.dtos.DataItemDTO;
import it.smartcommunitylabdhub.core.models.entities.DataItem;
import it.smartcommunitylabdhub.core.repositories.DataItemRepository;
import it.smartcommunitylabdhub.core.models.builders.dtos.DataItemDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.entities.DataItemEntityBuilder;
import it.smartcommunitylabdhub.core.services.context.interfaces.DataItemContextService;
import jakarta.transaction.Transactional;

@Service
public class DataItemContextServiceImpl extends ContextService implements DataItemContextService {

    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    DataItemDTOBuilder dataItemDTOBuilder;

    @Autowired
    DataItemEntityBuilder dataItemEntityBuilder;

    @Override
    public DataItemDTO createDataItem(String projectName, DataItemDTO dataItemDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // dataItemDTO
            if (!projectName.equals(dataItemDTO.getProject())) {
                throw new CustomException("Project Context and DataItem Project does not match", null);
            }

            // Check project context
            checkContext(dataItemDTO.getProject());

            // Check if dataItem already exist if exist throw exception otherwise create a
            // new one
            DataItem dataItem = (DataItem) Optional.ofNullable(dataItemDTO.getId())
                    .flatMap(id -> dataItemRepository.findById(id)
                            .map(a -> {
                                throw new CustomException(
                                        "The project already contains an dataItem with the specified UUID.", null);
                            }))
                    .orElseGet(() -> {
                        // Build an dataItem and store it in the database
                        DataItem newDataItem = dataItemEntityBuilder.build(dataItemDTO);
                        return dataItemRepository.save(newDataItem);
                    });

            // Return dataItem DTO
            return dataItemDTOBuilder.build(dataItem, false);

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<DataItemDTO> getLatestByProjectName(String projectName, Pageable pageable) {
        try {
            checkContext(projectName);

            Page<DataItem> dataItemPage = this.dataItemRepository
                    .findAllLatestDataItemsByProject(projectName,
                            pageable);
            return dataItemPage.getContent()
                    .stream()
                    .map((dataItem) -> {
                        return dataItemDTOBuilder.build(dataItem, false);
                    }).collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<DataItemDTO> getByProjectNameAndDataItemName(String projectName, String dataItemName,
            Pageable pageable) {
        try {
            checkContext(projectName);

            Page<DataItem> dataItemPage = this.dataItemRepository
                    .findAllByProjectAndNameOrderByCreatedDesc(projectName, dataItemName,
                            pageable);
            return dataItemPage.getContent()
                    .stream()
                    .map((dataItem) -> {
                        return dataItemDTOBuilder.build(dataItem, false);
                    }).collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public DataItemDTO getByProjectAndDataItemAndUuid(String projectName, String dataItemName,
            String uuid) {
        try {
            // Check project context
            checkContext(projectName);

            return this.dataItemRepository.findByProjectAndNameAndId(projectName, dataItemName, uuid).map(
                    dataItem -> dataItemDTOBuilder.build(dataItem, false))
                    .orElseThrow(
                            () -> new CustomException("The dataItem does not exist.", null));

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DataItemDTO getLatestByProjectNameAndDataItemName(String projectName, String dataItemName) {
        try {
            // Check project context
            checkContext(projectName);

            return this.dataItemRepository.findLatestDataItemByProjectAndName(projectName, dataItemName).map(
                    dataItem -> dataItemDTOBuilder.build(dataItem, false))
                    .orElseThrow(
                            () -> new CustomException("The dataItem does not exist.", null));

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DataItemDTO createOrUpdateDataItem(String projectName, String dataItemName, DataItemDTO dataItemDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // dataItemDTO
            if (!projectName.equals(dataItemDTO.getProject())) {
                throw new CustomException("Project Context and DataItem Project does not match.", null);
            }
            if (!dataItemName.equals(dataItemDTO.getName())) {
                throw new CustomException(
                        "Trying to create/update an dataItem with name different from the one passed in the request.",
                        null);
            }

            // Check project context
            checkContext(dataItemDTO.getProject());

            // Check if dataItem already exist if exist throw exception otherwise create a
            // new one
            DataItem dataItem = Optional.ofNullable(dataItemDTO.getId())
                    .flatMap(id -> {
                        Optional<DataItem> optionalDataItem = dataItemRepository.findById(id);
                        if (optionalDataItem.isPresent()) {
                            DataItem existingDataItem = optionalDataItem.get();

                            // Update the existing dataItem version
                            final DataItem dataItemUpdated = dataItemEntityBuilder.update(existingDataItem,
                                    dataItemDTO);
                            return Optional.of(this.dataItemRepository.save(dataItemUpdated));

                        } else {
                            // Build a new dataItem and store it in the database
                            DataItem newDataItem = dataItemEntityBuilder.build(dataItemDTO);
                            return Optional.of(dataItemRepository.save(newDataItem));
                        }
                    })
                    .orElseGet(() -> {
                        // Build a new dataItem and store it in the database
                        DataItem newDataItem = dataItemEntityBuilder.build(dataItemDTO);
                        return dataItemRepository.save(newDataItem);
                    });

            // Return dataItem DTO
            return dataItemDTOBuilder.build(dataItem, false);

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DataItemDTO updateDataItem(String projectName, String dataItemName, String uuid, DataItemDTO dataItemDTO) {

        try {
            // Check that project context is the same as the project passed to the
            // dataItemDTO
            if (!projectName.equals(dataItemDTO.getProject())) {
                throw new CustomException("Project Context and DataItem Project does not match", null);
            }
            if (!uuid.equals(dataItemDTO.getId())) {
                throw new CustomException(
                        "Trying to update an dataItem with an ID different from the one passed in the request.", null);
            }
            // Check project context
            checkContext(dataItemDTO.getProject());

            DataItem dataItem = this.dataItemRepository.findById(dataItemDTO.getId()).map(
                    a -> {
                        // Update the existing dataItem version
                        return dataItemEntityBuilder.update(a, dataItemDTO);
                    })
                    .orElseThrow(
                            () -> new CustomException("The dataItem does not exist.", null));

            // Return dataItem DTO
            return dataItemDTOBuilder.build(dataItem, false);

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteSpecificDataItemVersion(String projectName, String dataItemName, String uuid) {
        try {
            if (this.dataItemRepository.existsByProjectAndNameAndId(projectName, dataItemName, uuid)) {
                this.dataItemRepository.deleteByProjectAndNameAndId(projectName, dataItemName, uuid);
                return true;
            }
            throw new CoreException(
                    "DataItemNotFound",
                    "The dataItem you are trying to delete does not exist.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    "InternalServerError",
                    "cannot delete dataItem",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteAllDataItemVersions(String projectName, String dataItemName) {
        try {
            if (dataItemRepository.existsByProjectAndName(projectName, dataItemName)) {
                this.dataItemRepository.deleteByProjectAndName(projectName, dataItemName);
                return true;
            }
            throw new CoreException(
                    "DataItemNotFound",
                    "The dataItems you are trying to delete does not exist.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    "InternalServerError",
                    "cannot delete dataItem",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
