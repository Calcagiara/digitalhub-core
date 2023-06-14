"""
Workflow module.
"""
from sdk.entities.api import DTO_WKFL, create_api, update_api
from sdk.entities.base_entity import Entity, EntityMetadata, EntitySpec
from sdk.entities.run.run import Run
from sdk.utils.factories import get_context
from sdk.utils.utils import get_uiid


class WorkflowMetadata(EntityMetadata):
    ...


class WorkflowSpec(EntitySpec):
    def __init__(self, test: str = None, **kwargs) -> None:
        """
        Constructor.

        Parameters
        ----------
        **kwargs
            Additional keyword arguments.

        Notes
        -----
        If some of the attributes are not in the signature,
        they will be added as new attributes.
        """
        self.test = test

        # Set new attributes
        for k, v in kwargs.items():
            if k not in self.__dict__.keys():
                self.__setattr__(k, v)


class Workflow(Entity):
    """
    A class representing a workflow.
    """

    def __init__(
        self,
        project: str,
        name: str,
        kind: str = None,
        metadata: WorkflowMetadata = None,
        spec: WorkflowSpec = None,
        local: bool = False,
        embed: bool = False,
        uuid: str = None,
        **kwargs,
    ) -> None:
        """
        Initialize the Workflow instance.

        Parameters
        ----------
        project : str
            Name of the project.
        name : str
            Name of the workflow.
        kind : str, optional
            Kind of the workflow, default is 'workflow'.
        metadata : WorkflowMetadata, optional
            Metadata for the workflow, default is None.
        spec : WorkflowSpec, optional
            Specification for the workflow, default is None.
        local: bool, optional
            Specify if run locally, default is False.
        embed: bool, optional
            Specify if embed, default is False.
        **kwargs
            Additional keyword arguments.
        """
        super().__init__()
        self.project = project
        self.name = name
        self.kind = kind if kind is not None else "local"
        self.metadata = (
            metadata if metadata is not None else WorkflowMetadata(name=name)
        )
        self.spec = spec if spec is not None else WorkflowSpec()
        self.embedded = embed
        self.id = uuid if uuid is not None else get_uiid()

        self._local = local

        # Set new attributes
        for k, v in kwargs.items():
            if k not in self._obj_attr:
                self.__setattr__(k, v)

        # Set context
        self.context = get_context(self.project)

    #############################
    #  Save / Export
    #############################

    def save(self, uuid: str = None) -> dict:
        """
        Save workflow into backend.

        Parameters
        ----------
        uuid : str, optional
            UUID of the workflow to update, default is None.

        Returns
        -------
        dict
            Mapping representation of Workflow from backend.

        """
        if self._local:
            raise Exception("Use .export() for local execution.")

        obj = self.to_dict()

        if uuid is None:
            api = create_api(self.project, DTO_WKFL)
            return self.context.client.create_object(obj, api)

        self.id = uuid
        api = update_api(self.project, DTO_WKFL, self.name, uuid)
        return self.context.client.update_object(obj, api)

    def export(self, filename: str = None) -> None:
        """
        Export object as a YAML file.

        Parameters
        ----------
        filename : str, optional
            Name of the export YAML file. If not specified, the default value is used.

        Returns
        -------
        None

        """
        obj = self.to_dict()
        filename = (
            filename
            if filename is not None
            else f"workflow_{self.project}_{self.name}.yaml"
        )
        return self.export_object(filename, obj)

    #############################
    #  Workflow Methods
    #############################

    def run(self) -> "Run":
        ...

    def schedule(self) -> "Run":
        ...

    #############################
    #  Getters and Setters
    #############################

    @property
    def local(self) -> bool:
        """
        Get local flag.
        """
        return self._local

    #############################
    #  Generic Methods
    #############################

    @classmethod
    def from_dict(cls, d: dict) -> "Workflow":
        """
        Create Workflow instance from a dictionary.

        Parameters
        ----------
        d : dict
            Dictionary to create Workflow from.

        Returns
        -------
        Workflow
            Workflow instance.

        """
        project = d.get("project")
        name = d.get("name")
        uuid = d.get("id")
        if project is None or name is None:
            raise Exception("Project or name are not specified.")
        metadata = WorkflowMetadata.from_dict(d.get("metadata", {"name": name}))
        spec = WorkflowSpec.from_dict(d.get("spec", {}))
        return cls(project, name, metadata=metadata, spec=spec, uuid=uuid)
