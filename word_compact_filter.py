import os
import re

# Utilitzem el nom del fitxer que has pujat
INPUT_FILE_PATH = "/Users/arnau.torrents/Downloads/DISC22/DISC2-Compacte.txt"
OUTPUT_FILE_NAME = "PARAULES_5_SENSE_ACCENTS.txt"
TARGET_LENGTH = 5

def normalize_text(text):
    """
    Converteix el text a majúscules i elimina tots els accents (diacrítics i gràfics)
    i la dièresi, substituint la 'L·L' per 'L L'.
    """
    text = text.upper()

    # Substituir la 'L·L' per 'L L' per mantenir-les (si no, perdrien una lletra en el filtre)
    text = re.sub(r'L·L', 'LL', text)

    # Substitució dels accents i dièresi:
    replacements = {
        'À': 'A', 'É': 'E', 'È': 'E', 'Í': 'I', 'Ï': 'I',
        'Ó': 'O', 'Ò': 'O', 'Ú': 'U', 'Ü': 'U'
    }

    for accented, unaccented in replacements.items():
        text = text.replace(accented, unaccented)

    return text

def filter_words_by_length_and_type(input_filepath, output_filepath, target_length):
    """
    Llegeix un fitxer amb format 'paraula|categoria|codi',
    filtra per 5 lletres, exclou verbs, i normalitza (majúscules sense accents).
    """
    final_word_set = set()
    words_written = 0

    try:
        with open(input_filepath, 'r', encoding='utf-8') as infile:
            for line in infile:
                # Intentem dividir la línia pel separador '|'
                parts = line.strip().split('|')

                if len(parts) >= 2:
                    word_original = parts[0].strip()
                    category = parts[1].strip().lower()

                    # 1. Normalització de la paraula (majúscules i sense accents)
                    word_normalized = normalize_text(word_original)

                    # 2. Comprovació que NO és un verb (la categoria no comença per 'v.')
                    is_not_a_verb = not category.startswith('v.')

                    # 3. Comprovació de longitud i contingut (només alfabètic)
                    is_correct_length = len(word_normalized) == target_length
                    is_alphabetic = word_normalized.isalpha()

                    if is_correct_length and is_not_a_verb and is_alphabetic:
                        final_word_set.add(word_normalized)

    except FileNotFoundError:
        print(f"ERROR: El fitxer d'entrada no s'ha trobat a la ruta: {input_filepath}")
        return

    # Escriure el resultat al fitxer de sortida
    try:
        with open(output_filepath, 'w', encoding='utf-8') as outfile:
            for word in sorted(list(final_word_set)):
                outfile.write(word + '\n')
                words_written += 1
    except IOError:
        print(f"ERROR: No es pot escriure al fitxer de sortida: {output_filepath}")
        return

    print("--- Procés Finalitzat amb Èxit! ---")
    print(f"Paraules úniques de {target_length} lletres (no verbs) i sense accents trobades: {words_written}")
    print(f"El resultat s'ha desat a: {output_filepath}")


if __name__ == "__main__":
    # Assegura't que el fitxer DISC2-Compacte.txt és a la mateixa carpeta.
    filter_words_by_length_and_type(INPUT_FILE_PATH, OUTPUT_FILE_NAME, TARGET_LENGTH)