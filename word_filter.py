import os

def filter_words(input_filepath, output_filepath, target_length=5):
    """
    Llegeix un fitxer de text, filtra les paraules per longitud
    i les desa en un nou fitxer en majúscules.
    """

    # Canvia el separador si les teves paraules no estan per línia (per exemple, si hi ha comes)
    WORD_SEPARATOR = '\n'

    # Utilitzem un set per emmagatzemar les paraules úniques i evitar duplicats
    final_word_set = set()
    total_processed = 0

    try:
        with open(input_filepath, 'r', encoding='utf-8') as infile:
            for line in infile:
                total_processed += 1

                # Netejar i convertir a majúscules
                word = line.strip().upper()

                # Comprovació de longitud i contingut (només lletres)
                if len(word) == target_length and word.isalpha():
                    final_word_set.add(word)

    except FileNotFoundError:
        print(f"ERROR: El fitxer d'entrada no s'ha trobat a la ruta: {input_filepath}")
        return

    # Escriure el resultat al fitxer de sortida (ordenat per netedat)
    with open(output_filepath, 'w', encoding='utf-8') as outfile:
        # Utilitzem sorted() per ordre alfabètic
        outfile.write(WORD_SEPARATOR.join(sorted(final_word_set)))

    print("\nProcés Finalitzat amb Èxit!")
    print(f"Paraules úniques de {target_length} lletres trobades: {len(final_word_set)}")
    print(f"El resultat s'ha desat a: {output_filepath}")


if __name__ == "__main__":
    # --- CANVIA AQUESTES DUES RUTES ---
    INPUT_FILE_PATH = "/Users/arnau.torrents/Downloads/DISC2/DISC2-LP.txt"  # RUTA D'ENTRADA
    OUTPUT_FILE_PATH = "/Users/arnau.torrents/Downloads/paraules5.txt" # RUTA DE SORTIDA
    # ----------------------------------

    filter_words(INPUT_FILE_PATH, OUTPUT_FILE_PATH, target_length=5)