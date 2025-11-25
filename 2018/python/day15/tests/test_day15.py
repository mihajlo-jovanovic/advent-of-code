import pytest
from day15 import p1


@pytest.mark.parametrize(
    "filename, expected",
    [
        # ("sample4.txt", 27730),
        ("sample5.txt", 36334),
        ("sample6.txt", 39514),
        ("sample7.txt", 27755),
        ("sample8.txt", 28944),
        ("sample9.txt", 18740),
        ("day15.txt", 207059),
    ],
)
def test_p1(filename, expected):
    result = p1(filename)

    assert result == expected
