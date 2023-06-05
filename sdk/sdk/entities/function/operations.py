"""
Function operations module.
"""
from sdk.client.factory import get_client
from sdk.entities.function.function import Function, FunctionMetadata, FunctionSpec
from sdk.entities.utils import file_importer
from sdk.entities.api import (
    API_DELETE_ALL,
    API_DELETE_VERSION,
    API_READ_LATEST,
    API_READ_VERSION,
    DTO_FUNC,
)


def new_function(
    project: str,
    name: str,
    description: str = None,
    kind: str = None,
    source: str = None,
    image: str = None,
    tag: str = None,
    handler: str = None,
    local: bool = False,
    save: bool = False,
) -> Function:
    """
    Create a Function instance with the given parameters.

    Parameters
    ----------
    project : str
        Name of the project.
    name : str
        Identifier of the Function.
    description : str, optional
        Description of the Function.
    kind : str, optional
        The type of the Function.
    source : str, optional
        Path to the Function's source code on the local file system or remote storage.
    image : str, optional
        Name of the Function's Docker image.
    tag : str, optional
        Tag of the Function's Docker image.
    handler : str, optional
        Function handler name.
    local : bool, optional
        Flag to determine if object has local execution.
    save : bool, optional
        Flag to determine if object will be saved.

    Returns
    -------
    Function
        Instance of the Function class representing the specified function.
    """
    meta = FunctionMetadata(name=name, description=description)
    spec = FunctionSpec(source=source, image=image, tag=tag, handler=handler)
    obj = Function(
        project=project, name=name, kind=kind, metadata=meta, spec=spec, local=local
    )
    if save:
        if local:
            obj.export()
        else:
            obj.save()
    return obj


def get_function(project: str, name: str, uuid: str = None) -> Function:
    """
    Retrieves function details from the backend.

    Parameters
    ----------
    client : Client
        The client for DHUB backend.
    project : str
        Name of the project.
    name : str
        The name of the function.
    uuid : str, optional
        UUID of function specific version.

    Returns
    -------
    Function
        An object that contains details about the specified function.

    Raises
    ------
    KeyError
        If the specified function does not exist.

    """
    if uuid is not None:
        api = API_READ_VERSION.format(project, DTO_FUNC, name, uuid)
    else:
        api = API_READ_LATEST.format(project, DTO_FUNC, name)
    r = get_client().get_object(api)

    return Function.from_dict(r)


def import_function(file: str) -> Function:
    """
    Import an Function object from a file using the specified file path.

    Parameters
    ----------
    file : str
        The absolute or relative path to the file containing the Function object.

    Returns
    -------
    Function
        The Function object imported from the file using the specified path.

    """
    d = file_importer(file)
    return Function.from_dict(d)


def delete_function(project: str, name: str, uuid: str = None) -> None:
    """
    Delete a function.

    Parameters
    ----------
    client : Client
        The client for DHUB backend.
    project : str
        Name of the project.
    name : str
        The name of the function.
    uuid : str, optional
        UUID of function specific version.

    Returns
    -------
    None
        This function does not return anything.
    """
    client = get_client()
    if uuid is not None:
        api = API_DELETE_VERSION.format(project, DTO_FUNC, name, uuid)
    else:
        api = API_DELETE_ALL.format(project, DTO_FUNC, name)
    return client.delete_object(api)
