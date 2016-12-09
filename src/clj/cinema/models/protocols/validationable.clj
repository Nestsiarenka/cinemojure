(ns cinema.models.protocols.validationable)

(defprotocol Validationable
  (validate-insert [this] "Validates record data when inserting")
  (validate-update [this] "Validates record data when updating")
  (validate-delete [this] "Validates record data when deleting"))
