"""
Workflow operations module.
"""
from __future__ import annotations

import typing

from sdk.context.builder import get_context
from sdk.entities.workflows.entity import workflow_from_dict, workflow_from_parameters
from sdk.utils.api import api_ctx_delete, api_ctx_read, api_ctx_update
from sdk.utils.commons import WKFL
from sdk.utils.io_utils import read_yaml

if typing.TYPE_CHECKING:
    from sdk.entities.workflows.entity import Workflow


def create_workflow(**kwargs) -> Workflow:
    """
    Create a new Workflow instance with the specified parameters.

    Parameters
    ----------
    **kwargs
        Keyword arguments.

    Returns
    -------
    Workflow
        An instance of the created workflow.
    """
    return workflow_from_parameters(**kwargs)


def create_workflow_from_dict(obj: dict) -> Workflow:
    """
    Create a new Workflow instance from a dictionary.

    Parameters
    ----------
    obj : dict
        Dictionary to create the Workflow from.

    Returns
    -------
    Workflow
        Workflow object.
    """
    return workflow_from_dict(obj)


def new_workflow(
    project: str,
    name: str,
    description: str | None = None,
    kind: str | None = None,
    test: str | None = None,
    embedded: bool = True,
    uuid: str | None = None,
    **kwargs,
) -> Workflow:
    """
    Create a new Workflow instance with the specified parameters.

    Parameters
    ----------
    project : str
        A string representing the project associated with this workflow.
    name : str
        The name of the workflow.
    description : str
        A description of the workflow.
    kind : str
        Kind of the object.
    spec : dict
        Specification of the object.
    embedded : bool
        Flag to determine if object must be embedded in project.
    uuid : str
        UUID.
    **kwargs
        Keyword arguments.

    Returns
    -------
    Workflow
        An instance of the created workflow.
    """
    obj = create_workflow(
        project=project,
        name=name,
        description=description,
        kind=kind,
        test=test,
        embedded=embedded,
        uuid=uuid,
        **kwargs,
    )
    obj.save()
    return obj


def get_workflow(project: str, name: str, uuid: str | None = None) -> Workflow:
    """
    Retrieves workflow details from the backend.

    Parameters
    ----------

    project : str
        Name of the project.
    name : str
        The name of the workflow.
    uuid : str
        UUID.

    Returns
    -------
    Workflow
        Object instance.
    """
    api = api_ctx_read(project, WKFL, name, uuid=uuid)
    obj = get_context(project).read_object(api)
    return workflow_from_dict(obj)


def import_workflow(file: str) -> Workflow:
    """
    Import an Workflow object from a file using the specified file path.

    Parameters
    ----------
    file : str
        Path to the file.

    Returns
    -------
    Workflow
        Object instance.
    """
    obj = read_yaml(file)
    return workflow_from_dict(obj)


def delete_workflow(project: str, name: str, uuid: str | None = None) -> dict:
    """
    Delete workflow from the backend. If the uuid is not specified, delete all versions.

    Parameters
    ----------
    project : str
        Name of the project.
    name : str
        The name of the workflow.
    uuid : str
        UUID.

    Returns
    -------
    dict
        Response from backend.
    """
    api = api_ctx_delete(project, WKFL, name, uuid=uuid)
    return get_context(project).delete_object(api)


def update_workflow(workflow: Workflow) -> dict:
    """
    Update a workflow.

    Parameters
    ----------
    workflow : Workflow
        The workflow to update.

    Returns
    -------
    dict
        Response from backend.
    """
    api = api_ctx_update(workflow.metadata.project, WKFL, workflow.metadata.name, uuid=workflow.id)
    return get_context(workflow.metadata.project).update_object(workflow.to_dict(), api)
