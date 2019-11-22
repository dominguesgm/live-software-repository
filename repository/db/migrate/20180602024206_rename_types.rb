class RenameTypes < ActiveRecord::Migration[5.1]
  def change

    rename_column :attributes, :type, :attribute_type 
    rename_column :arguments, :type, :argument_type

  end
end
