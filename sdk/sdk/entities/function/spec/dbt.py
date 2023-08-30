"""
Dbt Function specification module.
"""
from sdk.entities.function.spec.base import FunctionSpec
from sdk.entities.utils.utils import decode_string, encode_string
from sdk.utils.exceptions import EntityError


class FunctionSpecDBT(FunctionSpec):
    """
    Specification for a Function DBT.
    """

    def __init__(
        self,
        source: str = "",
        image: str | None = None,
        tag: str | None = None,
        handler: str | None = None,
        command: str | None = None,
        sql: str | None = None,
        **kwargs,
    ) -> None:
        """
        Constructor.

        Parameters
        ----------
        sql : str
            SQL query to run inside DBT.
        """
        super().__init__(
            source,
            image,
            tag,
            handler,
            command,
            **kwargs,
        )
        if sql is None:
            raise EntityError("SQL query must be provided.")

        self.dbt = {
            "sql": encode_string(sql),
        }

    def get_sql(self) -> str:
        """
        Get the SQL query.

        Returns
        -------
        str
            SQL query.
        """
        return self.dbt["sql"]

    def get_sql_decoded(self) -> str:
        """
        Get the decoded SQL query.

        Returns
        -------
        str
            Decoded SQL query.
        """
        return decode_string(self.get_sql())
