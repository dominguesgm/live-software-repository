class CreateIMethods < ActiveRecord::Migration[5.1]
  def change
    create_table :i_methods do |t|
      t.string :name
      t.integer :start_of_method
      t.integer :length_of_method
      t.integer :lines_of_code
      t.string :return_type
      t.integer :argument_count
      t.string :key
      t.references :i_class

      t.timestamps
    end
  end
end
