{ pkgs ? (import <nixpkgs> {}).pkgs }:

with pkgs;

mkShell {
  buildInputs = [
    openjdk11
    sbt
    ammonite
    scala
    metals
    docker-compose
    postgresql
  ];

  shellHook = ''
    # ...
  '';
}
