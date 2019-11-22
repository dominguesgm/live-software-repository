class ChangeHashAttributes < ActiveRecord::Migration[5.1]
  def change

    rename_column :i_classes, :hash, :class_hash 

  end
end
